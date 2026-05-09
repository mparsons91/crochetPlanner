package com.matthewparsons.hookline.data.local

import com.matthewparsons.hookline.domain.model.Pattern
import com.matthewparsons.hookline.domain.repository.SavedPattern
import kotlinx.serialization.json.Json

/**
 * Converts a stored [PatternEntity] back into a domain [SavedPattern] by
 * decoding the JSON blob. The denormalized columns are ignored on read; the
 * domain model is the source of truth once we deserialize.
 */
fun PatternEntity.toSavedPattern(json: Json): SavedPattern = SavedPattern(
    id = id,
    createdAtEpochMs = createdAtEpochMs,
    pattern = json.decodeFromString(Pattern.serializer(), patternJson),
)

/**
 * Encodes a [SavedPattern] for storage, copying selected fields into separate
 * columns so list queries don't need to parse the JSON blob.
 */
fun SavedPattern.toEntity(json: Json): PatternEntity = PatternEntity(
    id = id,
    createdAtEpochMs = createdAtEpochMs,
    patternJson = json.encodeToString(Pattern.serializer(), pattern),
    shapeName = pattern.input.shape.displayName,
    yarnNumber = pattern.input.yarn.number,
    totalBaseStitches = pattern.totalBaseStitches,
    estimatedYards = pattern.yarnEstimate.yards,
)
