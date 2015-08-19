# dashy

[![Join the chat at https://gitter.im/oomagnitude/dashy](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/oomagnitude/dashy?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Extensible dashboard webapp written in scalajs

Currently supports:
- timeseries
- graphs

Additional visualizations can be added

## to run

```
$ sbt
> project dashJVM
> ~re-start
```

Visit in the browser at: `http://localhost:8080`


## prerequisites

You need to specify a directory on your local machine where the JSON metrics to visualize are stored. Do this
by setting the environment variable `CLA_EXP`. The application will expect the following directory structure
under this root:

```
results/<expname>/<date>/
```

- `<expname>` is a human-readable name for the type of experiment being run
- `<date>` is a string-serialized ISO date in the form `YYYY-MM-dd-HH-mm-ss`

Under the `results/<expname>/<date>/` directory is a directory called `json` which contains individual metrics
in the form of timeseries. The filenames take the form `<metricname>.json`, which contains the timeseries data,
with each line of the file being a complete JSON object. There is another file next to it called `<metricname>.meta`
which contains meta information about the metric. 

The JSON files are serialized objects by the [µpickle](https://github.com/lihaoyi/upickle) library. The data 
objects are specified in the [metrics-shared](https://github.com/oomagnitude/metrics-shared)
project and take the form `DataPoint[T]`. The time raster is in arbitrary units called `timestep`. 



