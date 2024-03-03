package com.articoding.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne()
    private User owner;

    private String title;

    private String description;

    private int likes;

    private int timesPlayed;


    @ManyToMany(mappedBy = "levels")
    private List<ClassRoom> classRooms;

    @Column(columnDefinition = "TEXT")
    private String serializaArticodingLevel;

    private boolean publicLevel;

    private boolean active = true;

    public Level() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) { this.likes = likes;}

    public void incrLikes(){this.likes++;}
    public void decrLikes(){this.likes--;}

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int t) {
        this.timesPlayed = t;
    }

    public void increaseTimesPlayed(){this.timesPlayed++;}


    public List<ClassRoom> getClassRooms() {
        return classRooms;
    }

    public void setClassRooms(List<ClassRoom> classRooms) {
        this.classRooms = classRooms;
    }

    public boolean isPublicLevel() {
        return publicLevel;
    }

    public void setPublicLevel(boolean publicLevel) {
        this.publicLevel = publicLevel;
    }

    public String getSerializaArticodingLevel() {
        return serializaArticodingLevel;
    }

    public void setSerializaArticodingLevel(String serializaArticodingLevel) {
        this.serializaArticodingLevel = serializaArticodingLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
