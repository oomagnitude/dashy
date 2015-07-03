package com.oomagnitude.dash.server.filesystem

import com.oomagnitude.dash.server.Accessor

import scala.concurrent.ExecutionContextExecutor

class FilesystemAccessor(implicit override val executionContext: ExecutionContextExecutor) extends Accessor
    with FilesystemExperimentAccessor with FilesystemMetadataAccessor