package com.telegin.githubsearcher.adapters;

/**
 * Created by sergeytelegin
 */
interface IBindView<T> {
    void bind(T data, int i);
    IBindView copy();
}
