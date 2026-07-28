package net.grongubbe.tengoku.client.gpu.model;

import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;

import java.util.List;
import java.util.Objects;

public final class GpuModelPart {
    private final GpuMesh mesh;
    private final List<GpuMaterial> materials;

    public GpuModelPart(GpuMesh mesh, List<GpuMaterial> materials) {
        this.mesh = Objects.requireNonNull(mesh, "mesh");
        this.materials = List.copyOf(Objects.requireNonNull(materials, "materials"));

        validateMaterialSlots();
    }

    private void validateMaterialSlots() {
        for (var section : mesh.subMeshes()) {
            int slot = section.materialSlot();

            if (slot >= materials.size()) {
                throw new IllegalArgumentException(
                        """
                        Invalid material slot.

                        Mesh section requires material slot:
                        %d

                        Available materials:
                        %d
                        """.formatted(slot, materials.size())
                );
            }
        }
    }

    public GpuMesh mesh() {
        return mesh;
    }

    public List<GpuMaterial> materials() {
        return materials;
    }
}