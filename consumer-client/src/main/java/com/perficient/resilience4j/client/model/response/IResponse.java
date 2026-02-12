package com.perficient.resilience4j.client.model.response;

import java.util.List;

public interface IResponse<T, E> {
    List<T> getData();
    List<E> getErrors();
}