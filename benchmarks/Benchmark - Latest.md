# Benchmark Results

## System Information

- Publish Date: 2026-09-14
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

|      Function       |         Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |
|---------------------|--------------------------------|--------------------------------|--------------------------------|
|           Creation  |    0,569 ns/op<br>Error 0,008 ±|    0,598 ns/op<br>Error 0,013 ±|    0,577 ns/op<br>Error 0,005 ±|
|    MatrixTransform  |    0,554 ns/op<br>Error 0,003 ±|    0,584 ns/op<br>Error 0,036 ±|    0,561 ns/op<br>Error 0,015 ±|
|  StandardOperation  |    1,803 ns/op<br>Error 0,009 ±|    2,612 ns/op<br>Error 0,139 ±|     0,614 ns/op<br>Error 0,01 ±|

### Matrix4x3fBenchmarks

|      Function       |         Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |    Lidiuma<br>Math<br>Score     |
|---------------------|--------------------------------|--------------------------------|--------------------------------|---------------------------------|
|      BoneAnimation  |    3,778 ns/op<br>Error 0,021 ±|    3,439 ns/op<br>Error 0,126 ±|      3,4 ns/op<br>Error 0,094 ±|    33,183 ns/op<br>Error 1,327 ±|
|           Creation  |    0,459 ns/op<br>Error 0,008 ±|    0,513 ns/op<br>Error 0,006 ±|    0,494 ns/op<br>Error 0,005 ±|     1,536 ns/op<br>Error 0,085 ±|
|    MatrixTransform  |    0,562 ns/op<br>Error 0,011 ±|    0,602 ns/op<br>Error 0,058 ±|    0,568 ns/op<br>Error 0,033 ±|     1,109 ns/op<br>Error 0,018 ±|
|  StandardOperation  |     1,754 ns/op<br>Error 0,01 ±|    2,597 ns/op<br>Error 0,081 ±|    0,469 ns/op<br>Error 0,021 ±|    24,498 ns/op<br>Error 0,915 ±|

## Vector

### Vector3Float

|   Function    |         Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |    Lidiuma<br>Math<br>Score    |
|---------------|--------------------------------|--------------------------------|--------------------------------|--------------------------------|
|        Angle  |     0,575 ns/op<br>Error 0,07 ±|    0,688 ns/op<br>Error 0,027 ±|    0,642 ns/op<br>Error 0,031 ±|     0,69 ns/op<br>Error 0,005 ±|
|     Creation  |     0,21 ns/op<br>Error 0,004 ±|     0,215 ns/op<br>Error 0,02 ±|    0,215 ns/op<br>Error 0,006 ±|    0,757 ns/op<br>Error 0,029 ±|
|  ExampleCase  |    0,631 ns/op<br>Error 0,118 ±|    0,604 ns/op<br>Error 0,008 ±|     0,56 ns/op<br>Error 0,021 ±|     0,945 ns/op<br>Error 0,01 ±|

