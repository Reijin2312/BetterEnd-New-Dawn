package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.world.structures.features.*;
import org.betterx.betterend.world.structures.piece.*;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureManager;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class EndStructures {
    public static final StructurePieceType VOXEL_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("voxel"), VoxelPiece::new);
    public static final StructurePieceType MOUNTAIN_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("mountain_piece"), CrystalMountainPiece::new);
    public static final StructurePieceType CAVE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("cave_piece"), CavePiece::new);
    public static final StructurePieceType LAKE_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("lake_piece"), LakePiece::new);
    public static final StructurePieceType PAINTED_MOUNTAIN_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("painted_mountain_piece"), PaintedMountainPiece::new);
    public static final StructurePieceType NBT_PIECE = StructureManager.registerPiece(BetterEnd.C.mk("nbt_piece"), NBTPiece::new);


    public static final StructureKey.Simple<GiantMossyGlowshroomStructure> GIANT_MOSSY_GLOWSHROOM = StructureManager
            .structure(BetterEnd.C.mk("giant_mossy_glowshroom"), GiantMossyGlowshroomStructure::new)
            .step(Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Simple<MegaLakeStructure> MEGALAKE = StructureManager
            .structure(BetterEnd.C.mk("megalake"), MegaLakeStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<MegaLakeSmallStructure> MEGALAKE_SMALL = StructureManager
            .structure(BetterEnd.C.mk("megalake_small"), MegaLakeSmallStructure::new)
            .step(Decoration.LAKES);

    public static final StructureKey.Simple<MountainStructure> MOUNTAIN = StructureManager
            .structure(BetterEnd.C.mk("mountain"), MountainStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<PaintedMountainStructure> PAINTED_MOUNTAIN = StructureManager
            .structure(BetterEnd.C.mk("painted_mountain"), PaintedMountainStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<EternalPortalStructure> ETERNAL_PORTAL = StructureManager
            .structure(BetterEnd.C.mk("eternal_portal"), EternalPortalStructure::new)
            .step(Decoration.RAW_GENERATION);
    public static final StructureKey.Simple<GiantIceStarStructure> GIANT_ICE_STAR = StructureManager
            .structure(BetterEnd.C.mk("giant_ice_star"), GiantIceStarStructure::new)
            .step(Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.Jigsaw END_VILLAGE = StructureManager
            .jigsaw(BetterEnd.C.mk("end_village"))
            .step(Decoration.SURFACE_STRUCTURES);

    public static void register() {
        Configs.STRUCTURES_TOGGLE.registerStructures(
                GIANT_MOSSY_GLOWSHROOM,
                MEGALAKE,
                MEGALAKE_SMALL,
                MOUNTAIN,
                PAINTED_MOUNTAIN,
                ETERNAL_PORTAL,
                GIANT_ICE_STAR,
                END_VILLAGE
        );
    }

}
