package com.coursework.server;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import com.coursework.server.database.PublicUser;
import com.coursework.server.database.User;
import com.coursework.server.database.UsersDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class GetUserHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        Map<String, List<String>> params = ctx.queryParamMap();
        Integer id = null;
        String id_s = null;
        List<String> idList = params.get("id");
        if (idList != null && !idList.isEmpty()) id_s = idList.get(0);
        if (id_s != null) id = Integer.valueOf(id_s);
        if (id == null || id <= 0) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("incorrect id");
            return;
        }

        try {
            UsersDB usersDB = UsersDB.getInstance();
            User user = usersDB.getById(id.longValue());
            PublicUser publicUser = null;
            if (user != null) publicUser = new PublicUser(user);
            String response = new Gson().toJson(publicUser);
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


class UserAuthentication implements Handler {
    // TODO: Реализовать секретное хранение
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 минут

    @Override
    public void handle(Context ctx) {
        String body = ctx.body();

        try {
            Gson gson = new Gson();
            Map<String, String> info = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());
            String login = info.get("login");
            String password = info.get("password");
            if (login == null || password == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                return;
            }
            User user = UsersDB.getInstance().getByLogin(login);

            if (user == null || !user.checkPassword(password)) {
                ctx.status(HttpStatus.UNAUTHORIZED);
                return;
            }
            String jwt = Jwts.builder()
                    .setSubject(user.getLogin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                    .signWith(SECRET_KEY)
                    .compact();

            ctx.status(HttpStatus.OK);
            ctx.result(jwt);
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}