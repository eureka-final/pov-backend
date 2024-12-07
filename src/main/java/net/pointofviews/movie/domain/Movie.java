package net.pointofviews.movie.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.review.domain.Review;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String plot;

    private String poster;

    private String backdrop;

    private LocalDate released;

    private Integer tmdbId;

    @Convert(converter = FilmRatingConverter.class)
    private KoreanFilmRating filmRating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST)
    private final List<MovieCountry> countries = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private final List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST)
    private final List<MovieGenre> genres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST)
    private final List<MovieCrew> crews = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.PERSIST)
    private final List<MovieCast> casts = new ArrayList<>();

    @Builder
    private Movie(String title, String plot, String poster,
                  LocalDate released, Integer tmdbId, String backdrop, KoreanFilmRating filmRating) {
        Assert.notNull(title, "title must not be null");
        this.title = title;
        this.plot = plot;
        this.poster = poster;
        this.released = released;
        this.tmdbId = tmdbId;
        this.backdrop = backdrop;
        this.filmRating = filmRating;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void addCrew(MovieCrew crew) {
        this.crews.add(crew);
        crew.updateMovie(this);
    }

    public void addCast(MovieCast cast) {
        casts.add(cast);
        cast.updateMovie(this);
    }

    public void addGenre(MovieGenre movieGenre) {
        this.genres.add(movieGenre);
        movieGenre.updateMovie(this);
    }

    public void addCountry(MovieCountry movieCountry) {
        this.countries.add(movieCountry);
        movieCountry.updateMovie(this);
    }
}
