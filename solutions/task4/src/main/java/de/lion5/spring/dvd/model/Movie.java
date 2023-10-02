package de.lion5.spring.dvd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.lion5.spring.dvd.users.WebUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(name = "Movie.movies", // entity graph solution
        attributeNodes = {
                @NamedAttributeNode(value = "actors"),
                @NamedAttributeNode(value = "filmStudio"),
                @NamedAttributeNode(value = "createdBy")
        }) // entity graph solution// entity graph solution
public class Movie {
    @Id
    private Long id;
    //    @NotNull(message = "Title must be set")
    @NotEmpty(message = "Title not there")
    private String title;
    private boolean wonOscar;
    @Min(value = 1920, message = "Movies before 1920 are not considered!")
    @Max(value = 2022, message = "Movies after 2022 are not planned now!")
    private int releaseYear;
    @NotNull
    @Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Must be a valid URL to a picture.")
    private String coverImage;
    // default fetch type: LAZY
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_actor", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @JsonManagedReference
    private List<Actor> actors;
    // Due to merge, the film studio will be stored when it is not present in the database
    @ManyToOne(cascade = CascadeType.MERGE) // default fetch type: EAGER
    @JsonIgnoreProperties({"movies"})
    private FilmStudio filmStudio;
    @ManyToOne
    private WebUser createdBy;

    @Override
    public String toString() {
        return "";
    }
}
