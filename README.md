## JOML & JOML2 JMH Benchmark Repository

As JOML 2 is being developed it needs to be tracked how much the library is improving.    
So this repository is intended to provide an easy way to implement benchmarks and to also maybe provide a hub where a lot of benchmarks can be tracked.   

## Latest Benchmarks    

The benchmarks can be found in the Benchmarks directory.    

## TODOs    

More functions. Open for suggestions

## How to run benchmarks

Pull the repo. Make sure you have jdk25 installed (other jvm versions aren't tested atm)    
and run the following command:    
```
gradle jmh --no-daemon
```    

The Markdown output will be automatically generated and put into the "results/output" folder.    
On top of that the Raw Isolated results are copied into the "results/temp" folder.    
If Jmh is run later on it will automatically append/replace the ran benchmarks in the temp folder.    
So the entire thing doesn't have to be run.   

## How to setup the project

Simply clone the repo and import the gradle project.    
To generate the shadow jars you run a "setup" or "build" or "jmh" then they will auto generate    

## How to implement new Benchmarks

### Extend existing benchmarks

Create a function with the following schema    
```
@Benchmark
public MY_RESULT_TYPE testMY_FUNCTION() {
//	write your test in here.
}
```       
Return the type that is required as result for the return type.    
If void return the mutated object.    
All variables required should be class fields and init via a setup function and cloned in the call itself.   
To avoid JVM optimization via deleting the code and just leaving the result.   

### Create new Benchmarks (Class)

When you want to create new benchmark class the following has to be provided.    
```
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MY_BENCHMARK {

}
```    
@BenchmarkMode defines what the target is. Which mode is used depends on the test, but AverageTime is a good default.    
@OutputTimeUnit defines which units are used. As the library is rather fast Nanoseconds are required. If the function is slower you can also provide Microseconds    
@State defines what scope variables are going to be used in. These are relevant for Setup functions.    

### Create new benchmark (Library)

Please create a dedicated folder for the library.    
The idea of the folder format is benchmark/library/section/SpecificClass