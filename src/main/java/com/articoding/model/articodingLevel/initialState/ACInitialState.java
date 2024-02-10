package com.articoding.model.articodingLevel.initialState;

public class ACInitialState {
    private BlocklyBaseVariable variables;
    private ACBlocklyBlock block;

    public ACInitialState() {
    }

    public BlocklyBaseVariable getVariables() {
        return variables;
    }

    public void setVariables(BlocklyBaseVariable variables) {
        this.variables = variables;
    }

    public ACBlocklyBlock getBlock() {
        return block;
    }

    public void setBlock(ACBlocklyBlock block) {
        this.block = block;
    }
}
