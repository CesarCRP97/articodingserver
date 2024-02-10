package com.articoding.model.articodingLevel;


import javax.persistence.Column;

public class ACLevel {

    private ACActiveBlock activeblocks;
    private ACBoardState boardstate;

    @Column(columnDefinition = "TEXT")
    private String initialState;

    public ACLevel() {
    }

    public ACActiveBlock getActiveblocks() {
        return activeblocks;
    }

    public void setActiveblocks(ACActiveBlock activeblocks) {
        this.activeblocks = activeblocks;
    }

    public ACBoardState getBoardstate() {
        return boardstate;
    }

    public void setBoardstate(ACBoardState boardstate) {
        this.boardstate = boardstate;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }
}
