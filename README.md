# BiVeS-StatsGenerator

This tool was developed to study the evolution of computational models in public repositories.

## Run

### Java

To run the Java tool you either need to compile the sources or you need to download a pre-compiled binary.
Both options are very simple:

#### Compile the Code

As we're using Maven it should be easy to get compile the source code into a binary.
Just clone the project and `cd` into the root directory to call

    mvn package

This will download alle dependencies and compile the code to the `target/` directory.
You'll find a `target/BiVeS-statsgenerator-XXX.jar` that contains the compiled project and a `target/BiVeS-statsgenerator-XXX-jar-with-dependencies.jar` that contains the compiled project including all dependencies.

#### Get pre-compiled binaries

We offer pre-compiled binaries at our webserver [bin.sems.uni-rostock.de/BiVeS-statsgenerator/](http://bin.sems.uni-rostock.de/BiVeS-statsgenerator/).
The easiest is to download the latest `*-with-dependencies.jar`.
All binaries are signed using GPG.

#### Run the Binary

To run the StatsGenerator you just need to call the following command:

    java -jar statsgenerator.jar

See below for command line options.

### Docker

There is also a Docker container available at [binfalse/bives-statsgenerator](https://hub.docker.com/r/binfalse/bives-statsgenerator/).


### Command Line Options

By default the StatsGenerator

* dumps downloaded models to `/srv/modelstats/storage/REPOSITORY`
* uses `/srv/modelstats/working` to store temporary files
* stores the calculated numers in `/srv/modelstats/storage/stats`

That can of course be changed using the following command line options:

* `-s STORAGE` set the storage directory (default: `/srv/modelstats/storage`)
* `-w WORKING` set the working directory (default: `/srv/modelstats/working`)
* `-f` speed up the calculations and neglect every model that is bigger than 10M
* `-h` show a help

Appart from that you may want to adjust some parameters of the Jave virtual machine.
As the repositories are quite big and models in it tend to become more complex you may run into Java memory issues.
In that case you could pass the following flags to the Java runtime environment:

* `-XX:-UseGCOverheadLimit` to remove the garbage collectors overhead limit -- sometimes the GC has problems removing all the tiny things and runs into an "overhead" because he cannot free memory fast enough
* `-XX:+UseParallelGC` do garbage collection in parallel
* `-Xms4096M` give the JVM 4GB of mem initially
* `-Xmx65536M` allow up  to 64GB of mem

For example:

    java -XX:-UseGCOverheadLimit -XX:+UseParallelGC -Xms65536M -Xmx65536M -jar statsgenerator.jar


## LICENSE

    Copyright martin scharm <https://binfalse.de/contact/>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

