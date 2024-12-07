package net.pointofviews.movie.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.domain.CodeGroupEnum;
import net.pointofviews.common.service.CommonCodeService;
import net.pointofviews.country.domain.Country;
import net.pointofviews.movie.domain.*;
import net.pointofviews.movie.dto.request.CreateMovieRequest;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.movie.service.MovieService;
import net.pointofviews.people.domain.People;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.pointofviews.movie.dto.request.CreateMovieRequest.SearchCreditApiRequest.CastRequest;
import static net.pointofviews.movie.dto.request.CreateMovieRequest.SearchCreditApiRequest.CrewRequest;
import static net.pointofviews.movie.exception.MovieException.*;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final CommonCodeService commonCodeService;
    private final MovieCountryServiceImpl movieCountryServiceImpl;
    private final MoviePeopleServiceImpl moviePeopleServiceImpl;

    @Override
    @Transactional
    public void saveMovie(CreateMovieRequest request) {
        if (movieRepository.existsByTmdbId(request.tmdbId())) {
            throw duplicateMovie(request.tmdbId());
        }

        Movie movie = request.toMovieEntity();

        List<MovieGenre> movieGenres = convertStringsToMovieGenre(request.genres());
        movieGenres.forEach(movie::addGenre);

        List<MovieCast> casts = processMoviePeoples(
                request.peoples().cast(),
                CastRequest::toPeopleEntity,
                CastRequest::toMovieCastEntity,
                People::addCast
        );
        casts.forEach(movie::addCast);

        List<MovieCrew> crews = processMoviePeoples(
                request.peoples().crew(),
                CrewRequest::toPeopleEntity,
                CrewRequest::toMovieCrewEntity,
                People::addCrew
        );
        crews.forEach(movie::addCrew);

        List<MovieCountry> movieCountries = processMovieCountries(request.originCountries());
        movieCountries.forEach(movie::addCountry);

        movieRepository.save(movie);
    }

    private List<MovieGenre> convertStringsToMovieGenre(List<String> stringGenres) {
        return stringGenres.stream()
                .map(genre -> {
                    String genreCode = commonCodeService.convertCommonCodeDescriptionToCode(genre, CodeGroupEnum.MOVIE_GENRE);
                    return MovieGenre.builder().genreCode(genreCode).build();
                })
                .toList();
    }

    private <T, E> List<E> processMoviePeoples(
            List<T> requests,
            Function<T, People> toPeople,
            Function<T, E> toMovieEntity,
            BiConsumer<People, E> associateEntity
    ) {
        List<E> entities = new ArrayList<>();
        for (T request : requests) {
            People people = toPeople.apply(request);
            people = moviePeopleServiceImpl.savePeopleIfNotExists(people);
            E entity = toMovieEntity.apply(request);
            associateEntity.accept(people, entity);
            entities.add(entity);
        }

        return entities;
    }

    private List<MovieCountry> processMovieCountries(List<String> countryRequests) {
        return countryRequests.stream()
                .map(Country::new)
                .map(movieCountryServiceImpl::saveMovieCountries)
                .map(MovieCountry::new)
                .toList();
    }
}
