package net.benwoodworth.fastcraft.core.dependencies.inventory


/**
 * A builder that creates Minecraft items.
 */
interface ItemBuilder<TFcItem> {

    /**
     * Builds a new instance the item.
     *
     * @Return A new instance of the item
     */
    fun build(): TFcItem

    /**
     * Resets this builder and uses the values from this item.
     *
     * @Return This builder, for chaining
     */
    fun from(item: TFcItem): ItemBuilder<TFcItem>

    /**
     * Set the item type.
     *
     * @param typeId The Minecraft item type ID
     * @Return This builder, for chaining
     */
    fun setType(typeId: String): ItemBuilder<TFcItem>

    /**
     * Set the item amount.
     *
     * @param amount The item amount
     * @Return This builder, for chaining
     */
    fun setAmount(amount: Int): ItemBuilder<TFcItem>

    /**
     * Set the item's display name.
     *
     * @param displayName The display name
     * @Return This builder, for chaining
     */
    fun setDisplayName(displayName: String?): ItemBuilder<TFcItem>

    /**
     * Set the item's lore.
     *
     * @param lore The lore
     * @Return This builder, for chaining
     */
    fun setLore(vararg lore: List<String?>?): ItemBuilder<TFcItem>

    /**
     * Add an enchantment.
     *
     * @param enchantmentId The Minecraft enchantment type ID
     * @param level         The enchantment level
     * @Return This builder, for chaining
     */
    fun addEnchantment(enchantmentId: String, level: Int): ItemBuilder<TFcItem>
}
