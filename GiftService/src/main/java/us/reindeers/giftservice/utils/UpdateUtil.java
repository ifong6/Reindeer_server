package us.reindeers.giftservice.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UpdateUtil {

    public static <T> void updateIfPresent(T value, Consumer<T> setter){
        Optional.ofNullable(value).ifPresent(setter);
    }

    public static <T> void updateIfPresentAndNotEmpty(String value, Consumer<String> setter){
        Optional.ofNullable(value)
                .filter(v -> !v.isEmpty())
                .ifPresent(setter);
    }

    public static <T> void updateIfPresentAndNotEmpty(List<T> value, Consumer<List<T>> setter){
        Optional.ofNullable(value)
                .filter(v -> !v.isEmpty())
                .ifPresent(setter);
    }


}
