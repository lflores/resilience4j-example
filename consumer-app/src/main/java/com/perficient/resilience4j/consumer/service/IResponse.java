package com.perficient.resilience4j.consumer.service;

import java.util.List;

public interface IResponse<T,E> {
    List<E> getErrors();
    List<T> getData();
} 