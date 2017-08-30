package com.thewoolleyweb.grh.processor.api.gateway

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result

typealias FuelReturnValue = Triple<Request, Response, Result<String, Exception>>
