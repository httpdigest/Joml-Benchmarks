# Benchmark Results

## System Information

- Publish Date: 2026-09-14
- Consistent Data: Yes
	- OS=Microsoft Windows 10 build 19045
	- CPU=AMD Ryzen 9 7900 12-Core Processor
	- JDK=25.0.3 (OpenJDK 64-Bit Server VM 25.0.3+9-LTS)
	- Library options=[joml.fastmath=true, joml.sinLookup=true, joml.useFma=true, joml.useMathFma=true]
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

|      Function       |         Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |
|---------------------|--------------------------------|--------------------------------|--------------------------------|
|         ComposeTRS  |    3.876 ns/op<br>Error 0.186 ±|    4.484 ns/op<br>Error 0.238 ±|    3.783 ns/op<br>Error 0.063 ±|
|           Creation  |     2.92 ns/op<br>Error 0.052 ±|    3.087 ns/op<br>Error 0.342 ±|    2.922 ns/op<br>Error 0.126 ±|
|    MatrixTransform  |    2.539 ns/op<br>Error 0.129 ±|    2.629 ns/op<br>Error 0.094 ±|     2.25 ns/op<br>Error 0.145 ±|
|  StandardOperation  |    7.111 ns/op<br>Error 0.336 ±|    5.968 ns/op<br>Error 0.439 ±|    4.863 ns/op<br>Error 0.076 ±|

Allocation per operation:

|      Function       |       Joml<br>Alloc        |  Joml2<br>Fields<br>Alloc  | Joml2<br>Records<br>Alloc  |
|---------------------|----------------------------|----------------------------|----------------------------|
|         ComposeTRS  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |
|           Creation  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |
|    MatrixTransform  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |
|  StandardOperation  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |  80.0 B/op<br>Error 0.0 ±  |

### Matrix4x3fBenchmarks

|      Function       |          Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |     Lidiuma<br>Math<br>Score     |
|---------------------|---------------------------------|--------------------------------|--------------------------------|----------------------------------|
|      BoneAnimation  |    20.231 ns/op<br>Error 0.523 ±|    18.092 ns/op<br>Error 2.18 ±|    15.72 ns/op<br>Error 0.291 ±|    169.323 ns/op<br>Error 20.92 ±|
|         ComposeTRS  |     3.421 ns/op<br>Error 0.206 ±|    4.367 ns/op<br>Error 0.198 ±|    4.433 ns/op<br>Error 0.169 ±|    110.082 ns/op<br>Error 7.942 ±|
|           Creation  |     2.421 ns/op<br>Error 0.163 ±|    2.592 ns/op<br>Error 0.079 ±|    2.574 ns/op<br>Error 0.057 ±|      8.014 ns/op<br>Error 1.053 ±|
|    MatrixTransform  |     2.551 ns/op<br>Error 0.277 ±|    2.693 ns/op<br>Error 0.152 ±|    2.324 ns/op<br>Error 0.018 ±|       6.51 ns/op<br>Error 0.462 ±|
|  StandardOperation  |     5.778 ns/op<br>Error 0.494 ±|     5.562 ns/op<br>Error 1.54 ±|    5.096 ns/op<br>Error 0.174 ±|                        N/A       |

Allocation per operation:

|      Function       |        Joml<br>Alloc        |  Joml2<br>Fields<br>Alloc   |  Joml2<br>Records<br>Alloc  |     Lidiuma<br>Math<br>Alloc      |
|---------------------|-----------------------------|-----------------------------|-----------------------------|-----------------------------------|
|      BoneAnimation  |  68.16 B/op<br>Error 0.0 ±  |  68.16 B/op<br>Error 0.0 ±  |  68.16 B/op<br>Error 0.0 ±  |  1,469.787 B/op<br>Error 0.006 ±  |
|         ComposeTRS  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |      1,184.0 B/op<br>Error 0.0 ±  |
|           Creation  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |         96.0 B/op<br>Error 0.0 ±  |
|    MatrixTransform  |   24.0 B/op<br>Error 0.0 ±  |   24.0 B/op<br>Error 0.0 ±  |   24.0 B/op<br>Error 0.0 ±  |         72.0 B/op<br>Error 0.0 ±  |
|  StandardOperation  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |   64.0 B/op<br>Error 0.0 ±  |                              N/A  |

## Vector

### Vector3fBenchmarks

|   Function    |         Joml<br>Score          |    Joml2<br>Fields<br>Score    |   Joml2<br>Records<br>Score    |    Lidiuma<br>Math<br>Score    |
|---------------|--------------------------------|--------------------------------|--------------------------------|--------------------------------|
|        Angle  |    6.839 ns/op<br>Error 1.179 ±|    4.249 ns/op<br>Error 0.289 ±|    4.114 ns/op<br>Error 0.035 ±|    7.856 ns/op<br>Error 0.523 ±|
|     Creation  |    1.542 ns/op<br>Error 0.056 ±|    1.636 ns/op<br>Error 0.274 ±|    1.523 ns/op<br>Error 0.018 ±|    4.427 ns/op<br>Error 0.225 ±|
|  ExampleCase  |    2.832 ns/op<br>Error 0.073 ±|    3.097 ns/op<br>Error 0.181 ±|    2.671 ns/op<br>Error 0.061 ±|     5.195 ns/op<br>Error 1.23 ±|

Allocation per operation:

|   Function    |       Joml<br>Alloc        |  Joml2<br>Fields<br>Alloc  | Joml2<br>Records<br>Alloc  |  Lidiuma<br>Math<br>Alloc  |
|---------------|----------------------------|----------------------------|----------------------------|----------------------------|
|        Angle  |   0.0 B/op<br>Error 0.0 ±  |   0.0 B/op<br>Error 0.0 ±  |   0.0 B/op<br>Error 0.0 ±  |   0.0 B/op<br>Error 0.0 ±  |
|     Creation  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |  72.0 B/op<br>Error 0.0 ±  |
|  ExampleCase  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |  24.0 B/op<br>Error 0.0 ±  |  72.0 B/op<br>Error 0.0 ±  |

