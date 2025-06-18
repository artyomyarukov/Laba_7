package server.requets_processing;

import common.auth.LoginResponse;
import common.auth.User;
import common.commands.CommandBehavior;
import lombok.AllArgsConstructor;
import org.postgresql.util.PSQLException;
import server.services.UserService;


import java.sql.SQLException;
import java.util.Map;

@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final Map<String, CommandBehavior> clientCommandDefinitions;
    public LoginResponse register(User user){
        try {
            User userFromDb = userService.register(user);
            return new LoginResponse(null, clientCommandDefinitions, userFromDb.getId());
        } catch (PSQLException e){
            return new LoginResponse(new IllegalArgumentException("Пользователь с таким логином уже существует, выберите другой"), null, -1);
        } catch (Exception e){
            return new LoginResponse(e, null, -1);
        }
    }

    public LoginResponse login(User user){
        try {
            User userFromDb = userService.login(user);
            return new LoginResponse(null, clientCommandDefinitions, userFromDb.getId());
        } catch (Exception e){
            return new LoginResponse(e, null, -1);
        }
    }
}