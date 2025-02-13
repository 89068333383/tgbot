package commands;

public class BotCommonCommands {

    @AppBotCommand(name = "/hello", description = "when request hello", showInHelp = true)
    String hello(){
        return "hello? User";
    }
    @AppBotCommand(name = "/bye", description = "when request bye", showInHelp = true)
    String bye(){
        return "Good bye, user";
    }
    @AppBotCommand(name = "/userName", description = "when request help" , showInKeyboard = true)
    String userName(){
        return (String) hello();
    }
}
