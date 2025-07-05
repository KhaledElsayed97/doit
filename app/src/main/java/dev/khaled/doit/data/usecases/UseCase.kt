package dev.khaled.doit.data.usecases

interface UseCase<in P, R> {
    suspend operator fun invoke(parameters: P): R
}

interface NoParamsUseCase<R> {
    suspend operator fun invoke(): R
} 