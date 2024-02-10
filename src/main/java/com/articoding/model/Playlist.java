package com.articoding.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "playlist")
public class Playlist {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "playlist_levels",
            joinColumns = @JoinColumn(
                    name = "playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "level_id", referencedColumnName = "id")
    )
    List<Level> levels;

    String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    //TODO - Tabla de relación autor - playlist??
    @ManyToOne
    private User owner;
    @Column
    private boolean enabled = true;

    public Playlist() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}