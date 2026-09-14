# Benchmark Results

## System Information

- Publish Date: 2026-09-12
- OS: Microsoft Windows 10 build 19045
- CPU: AMD Ryzen 9 7900 12-Core Processor             
- Java Version: Eclipse Adoptium - OpenJDK Runtime Environment - Temurin-25.0.3+9
- Consistent Data: Yes
	- jmh=1.36
	- Warmup Iterations=2
	- Warmup Time=3 s
	- Iteratations=5
	- Iteration Time=2 s


## Libraries Tested
- JOML: 1.10.9
- JOML2 Fields: BETA


## Matrix

### Matrix4fBenchmarks

|      Function       |       Joml<br>Score       | Joml2<br>Fields<br>Score  | Joml2<br>Records<br>Score |
|---------------------|---------------------------|---------------------------|---------------------------|
|           Creation  |     0,569 ns/op<br>0,008 ±|     0,578 ns/op<br>0,025 ±|     0,577 ns/op<br>0,005 ±|
|    MatrixTransform  |     0,554 ns/op<br>0,003 ±|     0,583 ns/op<br>0,009 ±|     0,561 ns/op<br>0,015 ±|
|  StandardOperation  |     1,803 ns/op<br>0,009 ±|     2,675 ns/op<br>0,366 ±|      0,614 ns/op<br>0,01 ±|

### Matrix4x3fBenchmarks

|      Function       |       Joml<br>Score       | Joml2<br>Fields<br>Score  | Joml2<br>Records<br>Score |
|---------------------|---------------------------|---------------------------|---------------------------|
|      BoneAnimation  |     3,778 ns/op<br>0,021 ±|     3,486 ns/op<br>0,117 ±|       3,4 ns/op<br>0,094 ±|
|           Creation  |     0,459 ns/op<br>0,008 ±|     0,488 ns/op<br>0,003 ±|     0,494 ns/op<br>0,005 ±|
|    MatrixTransform  |     0,562 ns/op<br>0,011 ±|     0,583 ns/op<br>0,011 ±|     0,568 ns/op<br>0,033 ±|
|  StandardOperation  |      1,754 ns/op<br>0,01 ±|     2,613 ns/op<br>0,063 ±|     0,469 ns/op<br>0,021 ±|

## Vector

### Vector3Float

|   Function    |       Joml<br>Score       | Joml2<br>Fields<br>Score  | Joml2<br>Records<br>Score |
|---------------|---------------------------|---------------------------|---------------------------|
|        Angle  |      0,575 ns/op<br>0,07 ±|     0,686 ns/op<br>0,024 ±|     0,642 ns/op<br>0,031 ±|
|     Creation  |      0,21 ns/op<br>0,004 ±|      0,21 ns/op<br>0,005 ±|     0,215 ns/op<br>0,006 ±|
|  ExampleCase  |     0,631 ns/op<br>0,118 ±|     0,619 ns/op<br>0,029 ±|      0,56 ns/op<br>0,021 ±|

