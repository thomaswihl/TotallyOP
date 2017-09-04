package at.witho.totally_op;

import at.witho.totally_op.entity.Car;
import at.witho.totally_op.entity.RenderCar;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder("totally_op")
public class ModEntities {
    @SideOnly(Side.CLIENT)
    public static void initModels() {
        //RenderingRegistry.registerEntityRenderingHandler(Car.class, RenderCar.FACTORY);
    }
}
