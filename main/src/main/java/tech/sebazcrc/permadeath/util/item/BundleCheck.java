package tech.sebazcrc.permadeath.util.item;

import org.bukkit.inventory.meta.BundleMeta;

public abstract class BundleCheck implements BundleMeta {

    @Override
    public BundleCheck clone() {
        try {
            return (BundleCheck) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}

















