package com.articoding.model;

import javax.persistence.*;
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

    /** TODO-Eliminar */
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoration> valorationList;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;
     /** */



    @ManyToMany(mappedBy = "levels")
    private List<ClassRoom> classRooms;

    @Column(columnDefinition="TEXT")
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
/** TODO - Eliminar */
    public List<Valoration> getValorationList() {
        return valorationList;
    }

    public void setValorationList(List<Valoration> valorationList) {
        this.valorationList = valorationList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
 /**/

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
