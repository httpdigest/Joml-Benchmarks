# Benchmark Results

## System Information

- Publish Date: 2026-09-14
- Consistent Data: Yes
	- OS=Apple macOS 26.6.2 build 25G83
	- CPU=Apple M4 Max
	- JDK=25.0.2 (OpenJDK 64-Bit Server VM 25.0.2+10-69)
	- Library options=joml.fastmath=true joml.sinLookup=true joml.useFma=true joml.useMathFma=true
	- jmh=1.36
	- Threads=1
	- Forks=1
	- Warmup Iterations=2
	- Warmup Time=3 s
	- Iteratations=5
	- Iteration Time=2 s


## Libraries Tested
- JOML: 1.10.9
- JOML2 Fields: 1.0.0-SNAPSHOT
- JOML2 Records: 1.0.0-SNAPSHOT
- Lidiuma Math: 0.3.0-j17


## Matrix

### Matrix4fBenchmarks

|      Function       |      Joml<br>Score       | Joml2<br>Fields<br>Score | Joml2<br>Records<br>Score |
|---------------------|--------------------------|--------------------------|---------------------------|
|         ComposeTRS  |    6.228 ns/op<br>0.049 ±|    5.839 ns/op<br>0.021 ±|     4.939 ns/op<br>0.033 ±|
|           Creation  |    3.133 ns/op<br>0.005 ±|    2.954 ns/op<br>0.047 ±|     2.956 ns/op<br>0.048 ±|
|    MatrixTransform  |    3.126 ns/op<br>0.041 ±|    3.272 ns/op<br>0.027 ±|     3.012 ns/op<br>0.027 ±|
|  StandardOperation  |     8.63 ns/op<br>0.316 ±|     6.25 ns/op<br>0.026 ±|      6.059 ns/op<br>0.03 ±|

Allocation per operation:

|      Function       | Joml<br>Alloc | Joml2<br>Fields<br>Alloc | Joml2<br>Records<br>Alloc |
|---------------------|---------------|--------------------------|---------------------------|
|         ComposeTRS  |    80.0 B/op  |               80.0 B/op  |                80.0 B/op  |
|           Creation  |    80.0 B/op  |               80.0 B/op  |                80.0 B/op  |
|    MatrixTransform  |    24.0 B/op  |               24.0 B/op  |                24.0 B/op  |
|  StandardOperation  |    80.0 B/op  |               80.0 B/op  |                80.0 B/op  |

### Matrix4x3fBenchmarks

|      Function       |       Joml<br>Score       | Joml2<br>Fields<br>Score  | Joml2<br>Records<br>Score |  Lidiuma<br>Math<br>Score  |
|---------------------|---------------------------|---------------------------|---------------------------|----------------------------|
|      BoneAnimation  |    21.511 ns/op<br>0.159 ±|    15.705 ns/op<br>0.094 ±|    10.935 ns/op<br>0.164 ±|    185.496 ns/op<br>1.662 ±|
|         ComposeTRS  |     5.244 ns/op<br>0.041 ±|     5.584 ns/op<br>0.024 ±|     4.797 ns/op<br>0.051 ±|     115.24 ns/op<br>0.879 ±|
|           Creation  |     2.807 ns/op<br>0.047 ±|      2.663 ns/op<br>0.05 ±|     2.655 ns/op<br>0.017 ±|      5.826 ns/op<br>0.102 ±|
|    MatrixTransform  |       3.1 ns/op<br>0.072 ±|     3.296 ns/op<br>0.015 ±|     3.009 ns/op<br>0.021 ±|       6.547 ns/op<br>0.16 ±|
|  StandardOperation  |     6.119 ns/op<br>0.049 ±|     6.011 ns/op<br>0.048 ±|     5.878 ns/op<br>0.038 ±|                  N/A       |

Allocation per operation:

|      Function       | Joml<br>Alloc | Joml2<br>Fields<br>Alloc | Joml2<br>Records<br>Alloc | Lidiuma<br>Math<br>Alloc |
|---------------------|---------------|--------------------------|---------------------------|--------------------------|
|      BoneAnimation  |   68.16 B/op  |              68.16 B/op  |               68.16 B/op  |           1,469.79 B/op  |
|         ComposeTRS  |    64.0 B/op  |               64.0 B/op  |                64.0 B/op  |            1,040.0 B/op  |
|           Creation  |    64.0 B/op  |               64.0 B/op  |                64.0 B/op  |               96.0 B/op  |
|    MatrixTransform  |    24.0 B/op  |               24.0 B/op  |                24.0 B/op  |               72.0 B/op  |
|  StandardOperation  |    64.0 B/op  |               64.0 B/op  |                64.0 B/op  |                     N/A  |

## Vector

### Vector3Float

|   Function    |      Joml<br>Score       | Joml2<br>Fields<br>Score | Joml2<br>Records<br>Score | Lidiuma<br>Math<br>Score |
|---------------|--------------------------|--------------------------|---------------------------|--------------------------|
|        Angle  |     4.88 ns/op<br>0.063 ±|    3.289 ns/op<br>0.117 ±|     3.103 ns/op<br>0.031 ±|    5.809 ns/op<br>0.083 ±|
|     Creation  |    2.026 ns/op<br>0.013 ±|     2.026 ns/op<br>0.03 ±|     2.034 ns/op<br>0.014 ±|    6.777 ns/op<br>0.043 ±|
|  ExampleCase  |    2.441 ns/op<br>0.015 ±|    3.118 ns/op<br>0.096 ±|      2.46 ns/op<br>0.033 ±|    6.626 ns/op<br>0.063 ±|

Allocation per operation:

|   Function    | Joml<br>Alloc | Joml2<br>Fields<br>Alloc | Joml2<br>Records<br>Alloc | Lidiuma<br>Math<br>Alloc |
|---------------|---------------|--------------------------|---------------------------|--------------------------|
|        Angle  |     0.0 B/op  |                0.0 B/op  |                 0.0 B/op  |                0.0 B/op  |
|     Creation  |    24.0 B/op  |               24.0 B/op  |                24.0 B/op  |               72.0 B/op  |
|  ExampleCase  |    24.0 B/op  |               24.0 B/op  |                24.0 B/op  |               72.0 B/op  |

