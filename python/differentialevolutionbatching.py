# differential evolution search of the two-dimensional sphere objective function
import concurrent.futures
import time

from matplotlib import pyplot
from numpy.random import rand
from numpy.random import choice
from numpy import asarray
from numpy import clip
from numpy import argmin
from numpy import min
from numpy import around
import numpy as np

import multiprocessing


# define mutation operation
def mutation(x, F):
    return x[0] + F * (x[1] - x[2])


# define boundary check operation
def check_bounds(mutated, bounds):
    mutated_bound = [clip(mutated[i], bounds[i, 0], bounds[i, 1]) for i in range(len(bounds))]
    return mutated_bound


# define crossover operation
def crossover(mutated, target, dims, cr):
    # generate a uniform random value for every dimension
    p = rand(dims)
    # generate trial vector by binomial crossover
    trial = [mutated[i] if p[i] < cr else target[i] for i in range(dims)]
    return trial


# def differential_evolution(pop_size, bounds, iter, F, cr, func):
#     # initialise population of candidate solutions randomly within the specified bounds
#     pop = bounds[:, 0] + (rand(pop_size, len(bounds)) * (bounds[:, 1] - bounds[:, 0]))
#     # evaluate initial population of candidate solutions
#     obj_all = [func(ind) for ind in pop]
#
#     # find the best performing vector of initial population
#     best_vector = pop[argmin(obj_all)]
#     best_obj = min(obj_all)
#     prev_obj = best_obj
#     # run iterations of the algorithm
#     for i in range(iter):
#         # iterate over all candidate solutions
#         for j in range(pop_size):
#             # choose three candidates, a, b and c, that are not the current one
#             candidates = [candidate for candidate in range(pop_size) if candidate != j]
#             a, b, c = pop[choice(candidates, 3, replace=False)]
#             # perform mutation
#             mutated = mutation([a, b, c], F)
#             # check that lower and upper bounds are retained after mutation
#             mutated = check_bounds(mutated, bounds)
#             # perform crossover
#             trial = crossover(mutated, pop[j], len(bounds), cr)
#             # compute objective function value for target vector
#             # obj_target = func(pop[j]) # original which called the function unnecessarily
#             obj_target = obj_all[j]
#             # compute objective function value for trial vector
#             obj_trial = func(trial)
#             # perform selection
#             if obj_trial < obj_target:
#                 # replace the target vector with the trial vector
#                 pop[j] = trial
#                 # store the new objective function value
#                 obj_all[j] = obj_trial
#         # find the best performing vector at each iteration
#         best_obj = min(obj_all)
#         # store the lowest objective function value
#         if best_obj < prev_obj:
#             best_vector = pop[argmin(obj_all)]
#             prev_obj = best_obj
#             # report progress at each iteration
#             print('Iteration: %d f([%s]) = %.5f' % (i, around(best_vector, decimals=5), best_obj))
#     return [best_vector, best_obj]

def differential_evolution(pop_size, bounds, iter, F, cr, func):
    # initialise population of candidate solutions randomly within the specified bounds
    pop = bounds[:, 0] + (rand(pop_size, len(bounds)) * (bounds[:, 1] - bounds[:, 0]))
    # evaluate initial population of candidate solutions
    obj_all = [func(ind) for ind in pop]

    # find the best performing vector of initial population
    best_vector = pop[argmin(obj_all)]
    best_obj = min(obj_all)
    prev_obj = best_obj

    # Define a function for parallel computation
    def process_candidate(j):
        # choose three candidates, a, b and c, that are not the current one
        candidates = [candidate for candidate in range(pop_size) if candidate != j]
        a, b, c = pop[choice(candidates, 3, replace=False)]
        # perform mutation
        mutated = mutation([a, b, c], F)
        # check that lower and upper bounds are retained after mutation
        mutated = check_bounds(mutated, bounds)
        # perform crossover
        trial = crossover(mutated, pop[j], len(bounds), cr)
        # compute objective function value for target vector
        obj_target = obj_all[j]
        # compute objective function value for trial vector
        obj_trial = func(trial)
        # perform selection
        if obj_trial < obj_target:
            # replace the target vector with the trial vector
            pop[j] = trial
            # store the new objective function value
            obj_all[j] = obj_trial

    # run iterations of the algorithm
    average_time = 0
    for i in range(iter):
        # Create a ThreadPoolExecutor for parallel processing
        start_time = time.time()
        with concurrent.futures.ThreadPoolExecutor() as executor:
            # Iterate over all candidate solutions and execute the computation in parallel
            executor.map(process_candidate, range(pop_size))

        end_time = time.time()
        average_time += end_time - start_time

        # find the best performing vector at each iteration
        best_obj = min(obj_all)
        # store the lowest objective function value
        if best_obj < prev_obj:
            best_vector = pop[argmin(obj_all)]
            prev_obj = best_obj
            # report progress at each iteration
            # print('Iteration: %d f([%s]) = %.5f' % (i, around(best_vector, decimals=5), best_obj))
    executor.shutdown(wait=True)
    print("Average time per iteration: ", average_time / iter)
    return [best_vector, best_obj]

def schwefel(x):
    x = np.asarray(x)
    n = len(x)
    term1 = 418.9829 * n - np.sum(x * np.sin(np.sqrt(np.abs(x))))
    return term1

def schwefel_2(x):
    x = np.asarray(x)
    n = len(x)
    return np.sum(x**2 - 10 * np.cos(2 * np.pi * x)) + 10 * n


if __name__ == "__main__":
    # define population size
    pop_size = 2
    # define lower and upper bounds for every dimension
    bounds = np.asarray([(-500.0, 500.0), (-500.0, 500.0), (-500.0, 500.0), (-500.0, 500.0)])
    # define number of iterations
    iter = 1000
    # define scale factor for mutation
    F = 0.5
    # define crossover rate for recombination
    cr = 0.7
    # define number of cores to use for parallelization
    cores = 4

    # perform differential evolution and measure the time elapsed
    for pop_size in range(1, 21):
        time_start = time.time()
        solution = differential_evolution(pop_size, bounds, iter, F, cr, schwefel)
        time_end = time.time()
        print('\nSolution: f([%s]) = %.5f' % (around(solution[0], decimals=5), solution[1]))
        print('Elapsed: %.5f seconds' % (time_end - time_start))

    # line plot of best objective function values
    # pyplot.plot(solution[1], '.-')
    # pyplot.xlabel('Improvement Number')
    # pyplot.ylabel('Evaluation f(x)')
    # pyplot.show()
