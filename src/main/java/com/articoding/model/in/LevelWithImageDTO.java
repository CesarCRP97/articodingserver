package com.articoding.model.in;

public class LevelWithImageDTO {
    private ILevel level;
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
