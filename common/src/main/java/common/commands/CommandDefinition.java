package common.commands;



/*
 enum для команд
 */

public enum CommandDefinition {
    add(ArgType.STRING, true),
    exit(false, true),
    show(),
    save(),
    remove_key(ArgType.STRING),
    update_id(ArgType.LONG, true),
    print_ascending(),
    remove_greater(true, false),
    add_if_max(ArgType.STRING, true),
    sum_of_meters_above_sea_level (ArgType.STRING),
    group_counting_by_population(ArgType.STRING),
    help(),
    info(),
    clear(),
    history(),
    execute_script(ArgType.STRING, false, true);
    private final ArgType argType;
    private final boolean hasElement;
    private final boolean isClient;
    CommandDefinition(ArgType argType, boolean hasElement, boolean isClient){
        this.argType = argType;
        this.hasElement = hasElement;
        this.isClient = isClient;
    }
    CommandDefinition(){
        this(null, false, false);
    }
    CommandDefinition(ArgType argType){
        this(argType, false, false);
    }
    CommandDefinition(ArgType argType, boolean hasElement){
        this(argType, hasElement, false);
    }
    CommandDefinition(boolean hasElement, boolean isClient){
        this(null, hasElement, isClient);
    }
    public boolean hasElement(){
        return this.hasElement;
    }
    public ArgType getArgType(){
        return this.argType;
    }
    public boolean hasArg(){return this.argType != null;}
    public boolean isClient(){
        return this.isClient;
    }
}
