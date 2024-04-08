package com.articoding.model.in;

public class LevelWithImageDTO {
    private ILevel level;

    //Todo - eliminar comentado o implementar
    /*

    private Integer id;
    private String title;
    private int likes;
    private int timesPlayed;
    private boolean publicLevel;
    private IUser owner;


    public LevelWithImageDTO(ILevel level){
        this.id = level.getId();
        this.title = level.getTitle();
        this.likes = level.getLikes();
        this.timesPlayed = level.getTimesPlayed();
        this.publicLevel = level.getPublicLevel();
        this.owner = level.getOwner();
    }
    **/

    private byte[] image;

    public ILevel getLevel() {
        return level;
    }

    public void setLevel(ILevel level) {
        this.level = level;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
