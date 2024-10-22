package com.jl.newshubapi.context;

public class UserContext {

    private static ThreadLocal<Long> user = new ThreadLocal<>();

    public static void setUser(Long user) {
        UserContext.user.set(user);
    }

    public static Long getUser() {
        return UserContext.user.get();
    }

    public static void clear() {
        UserContext.user.remove();
    }
}
