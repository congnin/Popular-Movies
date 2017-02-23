package com.blablaing.android.popular_movies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/22/17.
 */

public class Utility {
    public static int changeAdultToInt(Boolean adult) {
        if (adult)
            return 1;
        else
            return 0;
    }

    public static String changeGenreToString(List<Integer> integers) {
        String result = "";
        for (int i = 0; i < integers.size(); i++) {
            result += Integer.toString(integers.get(i));
        }
        return result;
    }
}
