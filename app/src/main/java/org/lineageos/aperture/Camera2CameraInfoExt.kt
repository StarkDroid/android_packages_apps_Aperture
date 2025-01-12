/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.aperture

import android.hardware.camera2.CameraCharacteristics
import androidx.camera.camera2.interop.Camera2CameraInfo

/**
 * We're adding this here since it's private. We're supposed to use
 * CameraCharacteristics.getPhysicalCameraIds() but it's not exposed by CameraX yet.
 */
val LOGICAL_MULTI_CAMERA_PHYSICAL_IDS: CameraCharacteristics.Key<ByteArray> =
    CameraCharacteristics.Key(
        "android.logicalMultiCamera.physicalIds",
        ByteArray::class.java
    )

/**
 * Return the set of physical camera ids that this logical {@link CameraDevice} is made
 * up of.
 *
 * If the camera device isn't a logical camera, return an empty set.
 */
val Camera2CameraInfo.physicalCameraIds: Set<String>
    @androidx.camera.camera2.interop.ExperimentalCamera2Interop
    get() {
        val availableCapabilities = getCameraCharacteristic(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
        ) ?: throw AssertionError(
            "android.request.availableCapabilities must be non-null in the characteristics"
        )
        if (!availableCapabilities.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA
            )
        ) {
            return setOf()
        }

        val physicalCamIds: ByteArray = getCameraCharacteristic(
            LOGICAL_MULTI_CAMERA_PHYSICAL_IDS
        ) ?: throw AssertionError(
            "android.logicalMultiCamera.physicalIds must be non-null in the characteristics"
        )

        val physicalCamIdString = String(physicalCamIds, Charsets.UTF_8)
        val physicalCameraIdArray = physicalCamIdString.split(0.toChar())

        return physicalCameraIdArray.toSet()
    }
