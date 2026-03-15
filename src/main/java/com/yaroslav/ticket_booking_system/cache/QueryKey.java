package com.yaroslav.ticket_booking_system.cache;

import java.util.Arrays;
import java.util.Objects;

public class QueryKey {
    private final String methodName;
    private final Object[] params;

    public QueryKey(String methodName, Object... params) {
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final QueryKey queryKey = (QueryKey) o;
        return Objects.equals(methodName, queryKey.methodName) &&
                Arrays.deepEquals(params, queryKey.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(methodName);
        result = 31 * result + Arrays.deepHashCode(params);
        return result;
    }

    @Override
    public String toString() {
        return methodName + "(" + Arrays.toString(params) + ")";
    }
}
