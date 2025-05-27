package org.example;

import java.util.*;

public class WishlistManager {
    private static final Map<String, List<String>> map = new HashMap<>();

    public static List<String> getWishlist(String user) {
        return map.computeIfAbsent(user, k -> new ArrayList<>());
    }

    public static void add(String user, String movie) {
        List<String> list = getWishlist(user);
        if (!list.contains(movie)) list.add(movie);
    }

    public static void remove(String user, String movie) {
        getWishlist(user).remove(movie);
    }
}
