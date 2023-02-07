package org.valkyrienskies.tournament.items

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.ship.TournamentShipControl
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentItems

class PulseGun : Item(
    Properties().stacksTo(1).tab(TournamentItems.TAB)
){

    private var pulseForce : Vec3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        val force = TournamentConfig.SERVER.pulseGunForce

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = Vec3d(context.clickLocation)

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS

        pulseForce = Vec3d(player.lookAngle).normalize().mul(force * ship.inertiaData.mass)

        TournamentShipControl.getOrCreate(ship).addPulse(blockLocation, pulseForce!!)

        return super.useOn(context)
    }
}