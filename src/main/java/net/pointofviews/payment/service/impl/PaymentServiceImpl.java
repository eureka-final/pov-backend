package net.pointofviews.payment.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.toss.TossClientManager;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.payment.domain.Payment;
import net.pointofviews.payment.domain.TempPayment;
import net.pointofviews.payment.dto.request.ConfirmPaymentRequest;
import net.pointofviews.payment.dto.response.ConfirmPaymentResponse;
import net.pointofviews.payment.exception.PaymentException;
import net.pointofviews.payment.repository.PaymentRepository;
import net.pointofviews.payment.repository.TempPaymentRepository;
import net.pointofviews.payment.service.PaymentService;
import net.pointofviews.premiere.repository.EntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.pointofviews.member.exception.MemberException.memberNotFound;
import static net.pointofviews.payment.exception.PaymentException.amountMismatch;
import static net.pointofviews.payment.exception.PaymentException.paymentMismatch;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TossClientManager tossClient;
    private final PaymentRepository paymentRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final EntryRepository entryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void confirmPayment(Member loginMember, ConfirmPaymentRequest request) {

        Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> memberNotFound(loginMember.getId()));

        TempPayment tempPayment = tempPaymentRepository.findByOrderId(request.orderId())
                .orElseThrow(PaymentException::tempPaymentNotFound);

        if (!tempPayment.getMember().getId().equals(member.getId())) {
            throw paymentMismatch();
        }

        if (!tempPayment.getAmount().equals(request.amount())) {
            throw amountMismatch();
        }

        ConfirmPaymentResponse response = tossClient.confirmPayment(request);

        if (response.status().equals("DONE")) {
            Payment payment = Payment.builder()
                    .paymentKey(response.paymentKey())
                    .vendor("TOSS")
                    .amount(response.totalAmount())
                    .requestedAt(response.requestedAt())
                    .approvedAt(response.approvedAt())
                    .build();

            paymentRepository.save(payment);

        } else {
            cancelPayment(response.paymentKey(), "결제 취소");
            entryRepository.deleteByOrderId(response.orderId());
        }
    }

    @Transactional
    public void cancelPayment(String paymentKey, String cancelReason) {
        tossClient.cancelPayment(paymentKey, cancelReason);
    }

}