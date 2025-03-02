package com.example.imdbdataset.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.example.imdbdataset.exception.InvalidParameterException;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtil {

    /**
     * Retrieves a specific page of data from a given stream based on the provided page number and size.
     *
     * @param stream The stream from which to retrieve the page.
     * @param page   The zero-based index of the page to retrieve.
     * @param size   The number of elements to include in each page.
     * @param <T>    The type of elements in the stream.
     * @return A list containing the elements of the specified page.
     * @throws InvalidParameterException If the page or size parameters are invalid.
     */
    public static <T> List<T> getPage(Stream<T> stream, int page, int size) {
        validatePaginationParams(page, size);
        return stream
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    /**
     * Validates the pagination parameters.
     *
     * @param page The zero-based index of the page to retrieve.
     * @param size The number of elements to include in each page.
     * @throws InvalidParameterException If the page or size parameters are invalid.
     */
    public static void validatePaginationParams(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidParameterException("Page must be >= 0 and size must be > 0");
        }
    }
}