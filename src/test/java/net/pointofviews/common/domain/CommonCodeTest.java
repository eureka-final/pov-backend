package net.pointofviews.common.domain;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCodeTest {

	@Nested
	class Constructor {

		@Nested
		class Success {

			@Test
			void CommonCode_객체_생성() {
			    // given -- 테스트의 상태 설정
			    CommonCodeGroup groupCode = mock(CommonCodeGroup.class);
				String code = "01";
				String name = "Action";
				String description = "액션";

			    // when -- 테스트하고자 하는 행동
				CommonCode commonCode = CommonCode.builder()
					.code(code)
					.groupCode(groupCode)
					.name(name)
					.description(description)
					.build();

			    // then -- 예상되는 변화 및 결과
				assertSoftly(softly -> {
					softly.assertThat(commonCode.getCode()).isEqualTo(code);
					softly.assertThat(commonCode.getGroupCode()).isEqualTo(groupCode);
					softly.assertThat(commonCode.getName()).isEqualTo(name);
					softly.assertThat(commonCode.getDescription()).isEqualTo(description);
				});
			}
		}
	}
}