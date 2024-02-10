package com.articoding.model.articodingLevel.initialState;

public class ACBlocklyBlock {

    private String type;
    private String id;
    private String x;
    private String y;

    private ACBlockMutation mutation;
    private ACBlocklyField field;

    private ACBlockyStatement statement;
    private ACBlocklyValue value;
    private ACBlocklyNextBlock next;

    public ACBlocklyBlock() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public ACBlockMutation getMutation() {
        return mutation;
    }

    public void setMutation(ACBlockMutation mutation) {
        this.mutation = mutation;
    }

    public ACBlocklyField getField() {
        return field;
    }

    public void setField(ACBlocklyField field) {
        this.field = field;
    }

    public ACBlockyStatement getStatement() {
        return statement;
    }

    public void setStatement(ACBlockyStatement statement) {
        this.statement = statement;
    }

    public ACBlocklyValue getValue() {
        return value;
    }

    public void setValue(ACBlocklyValue value) {
        this.value = value;
    }

    public ACBlocklyNextBlock getNext() {
        return next;
    }

    public void setNext(ACBlocklyNextBlock next) {
        this.next = next;
    }
}

