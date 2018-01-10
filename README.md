# dashy

[![Join the chat at https://gitter.im/oomagnitude/dashy](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/oomagnitude/dashy?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Extensible dashboard webapp written in scalajs

Currently supports:
- timeseries
- graphs

Additional visualizations can be added

## to run

First, provide some JSON-formatted data and a root path to it by setting environment variable `METRICS_ROOT`. There is
example data in the [metrics-shared](https://github.com/oomagnitude/metrics-shared) project to get you started.

Next, you can run the project directly from sbt:

```
$ sbt
> project dashJVM
> ~re-start
```

Visit in the browser at: `http://localhost:8080`


## prerequisites

The metrics data objects are specified in the [metrics-shared](https://github.com/oomagnitude/metrics-shared)
project and take the form `DataPoint[T]`. The time raster is in arbitrary units called `timestep`. For details of
expected format for metrics data, see [metrics-shared](https://github.com/oomagnitude/metrics-shared).

The [metrics-shared](https://github.com/oomagnitude/metrics-shared) project also contains example data that can be
used to render charts in the dashboard application.


