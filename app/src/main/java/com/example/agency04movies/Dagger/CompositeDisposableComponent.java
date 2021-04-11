package com.example.agency04movies.Dagger;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Singleton
@Component(modules = CompositeDisposableModule.class)
public interface CompositeDisposableComponent {

    CompositeDisposable getCompositeDisposable();
}
