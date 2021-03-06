package chaos.mod.objects.block.slabs;

import chaos.mod.Eki;
import chaos.mod.init.BlockInit;
import chaos.mod.util.utils.UtilTranslatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockSlabBaseCommon extends BlockSlab {
	Block half;
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.<Variant>create("variant", Variant.class);

	public BlockSlabBaseCommon(String name, BlockSlab half) {
		super(Material.ROCK);
		setUnlocalizedName(UtilTranslatable.getEki(name));
		setCreativeTab(Eki.BLOCK);
		setRegistryName(name);
		useNeighborBrightness = !isDouble();

		IBlockState state = blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
		if (!isDouble())
			state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
		setDefaultState(state);

		this.half = half;

		BlockInit.BLOCKS.add(this);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
		if (!isDouble())
			state = state.withProperty(HALF, ((meta & 8) != 0) ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if (!isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP)
			meta |= 8;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if (!isDouble())
			return new BlockStateContainer(this, new IProperty[] { VARIANT, HALF });
		else
			return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return super.getUnlocalizedName();
	}

	@Override
	public IProperty<?> getVariantProperty() {
		return VARIANT;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return Variant.DEFAULT;
	}

	public static enum Variant implements IStringSerializable {
		DEFAULT;

		@Override
		public String getName() {
			return "default";
		}
	}

	@Override
	public boolean isDouble() {
		return false;
	}
}
