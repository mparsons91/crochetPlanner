package com.matthewparsons.hookline.data.local

import com.matthewparsons.hookline.domain.model.Pattern
import com.matthewparsons.hookline.domain.repository.SavedPattern
import com.matthewparsons.hookline.domain.repository.completedStitchCount
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val completedIndicesSerializer = SetSerializer(Int.serializer())

/**
 * Converts a stored [PatternEntity] back into a domain [SavedPattern] by
 * decoding the JSON blobs. The denormalized columns are ignored on read; the
 * domain model is the source of truth once we deserialize.
 */
fun PatternEntity.toSavedPattern(json: Json): SavedPattern = SavedPattern(
    id = id,
    createdAtEpochMs = createdAtEpochMs,
    pattern = json.decodeFromString(Pattern.serializer(), patternJson),
    completedStepIndices = json.decodeFromString(
        completedIndicesSerializer,
        completedStepIndicesJson,
    ),
)

/**
 * Encodes a [SavedPattern] for storage, copying selected fields into separate
 * columns so list queries don't need to parse the JSON blobs.
 */
fun SavedPattern.toEntity(json: Json): PatternEntity = PatternEntity(
    id = id,
    createdAtEpochMs = createdAtEpochMs,
    patternJson = json.encodeToString(Pattern.serializer(), pattern),
    shapeName = pattern.input.shape.displayName,
    yarnNumber = pattern.input.yarn.number,
    totalBaseStitches = pattern.totalBaseStitches,
    estimatedYards = pattern.yarnEstimate.yards,
    completedStepIndicesJson = json.encodeToString(
        completedIndicesSerializer,
        completedStepIndices,
    ),
    completedStitchCount = completedStitchCount,
    stepCount = pattern.steps.size,
)

/**
 * Encodes a set of indices for storage in the progress column. Used by the
 * repository's [updateCompletedSteps] without re-encoding the whole pattern.
 */
fun encodeCompletedIndices(indices: Set<Int>, json: Json): String =
    json.encodeToString(completedIndicesSerializer, indices)
