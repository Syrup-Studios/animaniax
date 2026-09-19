package net.syrupstudios.animaniax.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.PigBreedState;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.PeafowlMorphState;
import net.syrupstudios.animaniax.entity.HamsterEntity;

public final class AnimalSpawnEggItem extends Item {
    private static final String FORM_KEY = "AnimaniaxSpawnForm";
    private static final List<Form> FORMS = createForms();
    private static final List<String> GLOBAL_GROUPS = List.of("cow", "pig", "chicken", "goat", "sheep", "horse", "hamster", "rabbit", "peafowl");
    private static final Set<String> MALE_ONLY_GROUPS = Set.of("hamster");

    public AnimalSpawnEggItem() {
        super(new Properties().stacksTo(64));
    }

    public List<ItemStack> creativeForms() {
        return FORMS.stream().map(form -> stackFor(form.id())).toList();
    }

    public static int color(ItemStack stack, int tintIndex) {
        Form form = form(stack);
        if (form == null) return 0xFFFFFF;
        return tintIndex == 0 ? form.background() : tintIndex == 1 ? form.pattern() : 0xFFFFFF;
    }

    @Override public Component getName(ItemStack stack) {
        Form form = form(stack);
        return Component.translatable(form == null ? "item.animaniax.animal_spawn_egg.invalid"
                : "item.animaniax.animal_spawn_egg." + form.id());
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Form selected = form(stack);
        if (selected == null) return InteractionResult.FAIL;
        Player player = context.getPlayer();
        var level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        if (player != null && (!level.mayInteract(player, clicked)
                || !player.mayUseItemAt(clicked, context.getClickedFace(), stack))) return InteractionResult.FAIL;

        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel server)) return InteractionResult.FAIL;

        BlockState state = level.getBlockState(clicked);
        VoxelShape collision = state.getCollisionShape(level, clicked);
        BlockPos spawnPos = collision.isEmpty() ? clicked : clicked.relative(context.getClickedFace());
        Form resolved = selected.family().equals("global")
                ? randomGlobalForm(server) : selected.random() ? randomForm(server, selected.family()) : selected;
        Mob mob = spawn(server, stack, player, spawnPos, true,
                !clicked.equals(spawnPos) && context.getClickedFace() == net.minecraft.core.Direction.UP, resolved);
        if (mob == null) return InteractionResult.FAIL;
        if (player == null || !player.getAbilities().instabuild) stack.shrink(1);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, mob.position());
        return InteractionResult.CONSUME;
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Form form = form(stack);
        if (form != null) tooltip.add(Component.translatable("item.animaniax.animal_spawn_egg.details",
                form.breed().isEmpty() ? form.familyLabel() : form.breed(), form.kind().displayName));
    }

    private static Form randomForm(ServerLevel level, String family) {
        List<Form> options = FORMS.stream().filter(form -> form.family().equals(family) && !form.random()).toList();
        return options.get(level.getRandom().nextInt(options.size()));
    }

    private static Form randomGlobalForm(ServerLevel level) {
        Kind gender = switch (level.getRandom().nextInt(3)) {
            case 0 -> Kind.MALE;
            case 1 -> Kind.FEMALE;
            default -> Kind.CHILD;
        };
        List<String> groups = gender == Kind.MALE ? GLOBAL_GROUPS
                : GLOBAL_GROUPS.stream().filter(group -> !MALE_ONLY_GROUPS.contains(group)).toList();
        String group = groups.get(level.getRandom().nextInt(groups.size()));
        if (!MALE_ONLY_GROUPS.contains(group)) {
            String family = group.equals("horse") ? "draft_horse" : group;
            List<Form> forms = FORMS.stream().filter(form -> form.family().equals(family)
                    && form.kind() == gender && !form.random()).toList();
            return forms.get(level.getRandom().nextInt(forms.size()));
        }

        return switch (group) {
            case "hamster" -> oneOff("hamster", "Hamster", "Standard", HamsterEntity.TYPE);
            default -> throw new IllegalStateException("Unknown random animal group: " + group);
        };
    }

    private static Form oneOff(String family, String label, String breed, EntityType<? extends Mob> type) {
        return new Form("global_" + family + "_" + breedId(breed), family, label, breed,
                Kind.MALE, type, 0x80644F, 0xCDB8A1, false);
    }

    private static Form choose(ServerLevel level, Form... options) {
        return options[level.getRandom().nextInt(options.length)];
    }

    @SuppressWarnings("unchecked")
    private static Mob spawn(ServerLevel level, ItemStack stack, Player player, BlockPos pos,
            boolean alignPosition, boolean invertY, Form form) {
        EntityType<Mob> entityType = (EntityType<Mob>) form.entityType();
        Consumer<Entity> stackConfiguration = EntityType.appendDefaultStackConfig(entity -> {}, level, stack, player);
        Consumer<Mob> configure = mob -> {
            stackConfiguration.accept(mob);
            applyForm(mob, form);
            if (mob instanceof AnimalCareState care) care.animaniax$markPlayerInteraction(false);
        };
        return entityType.spawn(level, configure, pos, MobSpawnType.SPAWN_EGG, alignPosition, invertY);
    }

    private static void applyForm(Mob mob, Form form) {
        switch (form.family()) {
            case "cow" -> {
                if (form.entityType() != EntityType.MOOSHROOM && mob instanceof CowBreedState breed)
                    breed.animaniax$setBreed(form.breed());
            }
            case "pig" -> ((PigBreedState) mob).animaniax$setPigBreed(form.breed());
            case "sheep" -> {
                SheepBreedState sheep = (SheepBreedState) mob;
                sheep.animaniax$setSheepBreed(form.breed());
            }
            case "goat" -> ((GoatBreedState) mob).animaniax$setGoatBreed(form.breed());
            case "chicken" -> ((ChickenBreedState) mob).animaniax$setChickenBreed(form.breed());
            case "rabbit" -> ((RabbitBreedState) mob).animaniax$setRabbitBreed(form.breed());

            case "peafowl" -> ((PeafowlMorphState) mob).animaniax$setMorph(form.breed());
            default -> { }
        }

        if (form.kind() == Kind.FEMALE && mob instanceof AnimalReproductionState sex)
            sex.animaniax$setFemale(true);
        else if (form.kind() == Kind.MALE && mob instanceof AnimalReproductionState sex)
            sex.animaniax$setFemale(false);
        if (mob instanceof Chicken && mob instanceof ChickenSexState sex
                && form.kind() != Kind.CHILD
                && (form.family().equals("chicken") || form.family().equals("peafowl")))
            sex.animaniax$setHen(form.kind() == Kind.FEMALE);
        if (form.kind() == Kind.CHILD && mob instanceof AgeableMob ageable) ageable.setBaby(true);
    }

    private static ItemStack stackFor(String id) {
        ItemStack stack = new ItemStack(AnimalSpawnEggContent.ITEM);
        CompoundTag tag = new CompoundTag();
        tag.putString(FORM_KEY, id);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    private static Form form(ItemStack stack) {
        if (stack.getItem() != AnimalSpawnEggContent.ITEM) return null;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        String id = data.copyTag().getString(FORM_KEY);
        return FORMS.stream().filter(form -> form.id().equals(id)).findFirst().orElse(null);
    }

    private static List<Form> createForms() {
        List<Form> forms = new ArrayList<>();
        addCowBreed(forms, "angus", "Angus", EntityType.COW, 0x292929, 0xF4E6C7);
        addCowBreed(forms, "friesian", "Friesian", EntityType.COW, 0x222222, 0xF4F4F4);
        addCowBreed(forms, "hereford", "Hereford", EntityType.COW, 0xA44932, 0xE8D7B0);
        addCowBreed(forms, "highland", "Highland", EntityType.COW, 0x9D5738, 0xC6A77A);
        addCowBreed(forms, "holstein", "Holstein", EntityType.COW, 0x202020, 0xF4F4F4);
        addCowBreed(forms, "jersey", "Jersey", EntityType.COW, 0x825433, 0xD5B990);
        addCowBreed(forms, "longhorn", "Texas Longhorn", EntityType.COW, 0x95613D, 0xE7D7B7);
        addCowBreed(forms, "mooshroom", "Mooshroom", EntityType.MOOSHROOM, 0xC53B3B, 0xF2E5D0);
        addRandom(forms, "cow", "Cow", EntityType.COW, 0x825433, 0xF2E5D0);

        addBreed(forms, "pig", "duroc", "Duroc", EntityType.PIG, 0xA14E43, 0xEBC5B6, "piglet", "sow", "hog");
        addBreed(forms, "pig", "hampshire", "Hampshire", EntityType.PIG, 0x252525, 0xE8D9C5, "piglet", "sow", "hog");
        addBreed(forms, "pig", "large_black", "Large Black", EntityType.PIG, 0x252525, 0x343434, "piglet", "sow", "hog");
        addBreed(forms, "pig", "large_white", "Large White", EntityType.PIG, 0xEBD8D0, 0xF5EEE8, "piglet", "sow", "hog");
        addBreed(forms, "pig", "old_spot", "Old Spot", EntityType.PIG, 0xEBD8D0, 0x393939, "piglet", "sow", "hog");
        addBreed(forms, "pig", "yorkshire", "Yorkshire", EntityType.PIG, 0xEBD8D0, 0xD5C2B8, "piglet", "sow", "hog");
        addRandom(forms, "pig", "Pig", EntityType.PIG, 0xA14E43, 0xEBC5B6);

        addBreed(forms, "chicken", "leghorn", "Leghorn", EntityType.CHICKEN, 0xE8D6A8, 0xF5F0E2, "chick", "hen", "rooster");
        addBreed(forms, "chicken", "orpington", "Orpington", EntityType.CHICKEN, 0xB78045, 0xE5C18E, "chick", "hen", "rooster");
        addBreed(forms, "chicken", "plymouth_rock", "Plymouth Rock", EntityType.CHICKEN, 0x383838, 0xD7D0C2, "chick", "hen", "rooster");
        addBreed(forms, "chicken", "rhode_island_red", "Rhode Island Red", EntityType.CHICKEN, 0x8A392C, 0xB86A43, "chick", "hen", "rooster");
        addBreed(forms, "chicken", "wyandotte", "Wyandotte", EntityType.CHICKEN, 0x8B6A4D, 0xE6D7B8, "chick", "hen", "rooster");
        addRandom(forms, "chicken", "Chicken", EntityType.CHICKEN, 0xE8D6A8, 0xF5F0E2);

        addBreed(forms, "goat", "alpine", "Alpine", EntityType.GOAT, 0xC7B9A4, 0xEEE5D8, "kid", "doe", "buck");
        addBreed(forms, "goat", "angora", "Angora", EntityType.GOAT, 0xE7E1D7, 0xFFFFFF, "kid", "doe", "buck");
        addBreed(forms, "goat", "fainting", "Fainting", EntityType.GOAT, 0x8F755E, 0xCBB79F, "kid", "doe", "buck");
        addBreed(forms, "goat", "kiko", "Kiko", EntityType.GOAT, 0xD8D0C1, 0xF5F0E7, "kid", "doe", "buck");
        addBreed(forms, "goat", "kinder", "Kinder", EntityType.GOAT, 0x8E6850, 0xD0B797, "kid", "doe", "buck");
        addBreed(forms, "goat", "nigerian_dwarf", "Nigerian Dwarf", EntityType.GOAT, 0x38302B, 0xD9C8B2, "kid", "doe", "buck");
        addBreed(forms, "goat", "pygmy", "Pygmy", EntityType.GOAT, 0x573E31, 0xBFA286, "kid", "doe", "buck");
        addRandom(forms, "goat", "Goat", EntityType.GOAT, 0xC7B9A4, 0xEEE5D8);

        addBreed(forms, "sheep", "friesian", "Friesian", EntityType.SHEEP, 0x252525, 0xF5F5F5, "lamb", "ewe", "ram");
        addBreed(forms, "sheep", "suffolk", "Suffolk", EntityType.SHEEP, 0x3A3028, 0x9B785D, "lamb", "ewe", "ram");
        addBreed(forms, "sheep", "dorper", "Dorper", EntityType.SHEEP, 0x3A3028, 0xE9E2D6, "lamb", "ewe", "ram");
        addBreed(forms, "sheep", "dorset", "Dorset", EntityType.SHEEP, 0xD9C6A9, 0xF1E9DA, "lamb", "ewe", "ram");
        addBreed(forms, "sheep", "merino", "Merino", EntityType.SHEEP, 0xE8E2D7, 0xFFFFFF, "lamb", "ewe", "ram");
        addBreed(forms, "sheep", "jacob", "Jacob", EntityType.SHEEP, 0x3B332D, 0xE9E2D6, "lamb", "ewe", "ram");
        addRandom(forms, "sheep", "Sheep", EntityType.SHEEP, 0xE8E2D7, 0xFFFFFF);

        addBreed(forms, "rabbit", "cottontail", "Cottontail", EntityType.RABBIT, 0x8C755D, 0xD9C5A8, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "chinchilla", "Chinchilla", EntityType.RABBIT, 0x777777, 0xBDBDBD, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "dutch", "Dutch", EntityType.RABBIT, 0x44352B, 0xEEE4D6, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "havana", "Havana", EntityType.RABBIT, 0x60412F, 0x997454, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "jack", "Jack", EntityType.RABBIT, 0x8C755D, 0xD9C5A8, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "new_zealand", "New Zealand", EntityType.RABBIT, 0xE5DED5, 0xFFFFFF, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "rex", "Rex", EntityType.RABBIT, 0x705541, 0xBCA48B, "kit", "doe", "buck");
        addBreed(forms, "rabbit", "lop", "Lop", EntityType.RABBIT, 0x80644F, 0xCDB8A1, "kit", "doe", "buck");
        addRandom(forms, "rabbit", "Rabbit", EntityType.RABBIT, 0x80644F, 0xCDB8A1);

        addBreed(forms, "draft_horse", "draft", "Draft Horse", DraftHorseContent.DRAFT_HORSE,
                0x6D4C35, 0xC6A77A, "foal", "mare", "stallion");

        addBreed(forms, "peafowl", "blue", "Blue", PeafowlEntity.TYPE,
                0x315B9A, 0xD5C6A5, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "white", "White", PeafowlEntity.TYPE,
                0xE6E2D9, 0x74849A, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "taupe", "Taupe", PeafowlEntity.TYPE,
                0x82766A, 0xC5B8A7, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "purple", "Purple", PeafowlEntity.TYPE,
                0x67407E, 0xD3B96F, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "peach", "Peach", PeafowlEntity.TYPE,
                0xD48C69, 0xE9D394, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "opal", "Opal", PeafowlEntity.TYPE,
                0x5B8C96, 0xC8C9A1, "peachick", "peahen", "peacock");
        addBreed(forms, "peafowl", "charcoal", "Charcoal", PeafowlEntity.TYPE,
                0x343434, 0x8B7B99, "peachick", "peahen", "peacock");
        forms.add(new Form("peacock_random", "peafowl", "Peafowl", "", Kind.RANDOM,
                PeafowlEntity.TYPE, 0x315B9A, 0xD5C6A5, true));
        forms.add(new Form("random", "global", "Random Animal", "", Kind.RANDOM,
                EntityType.COW, 0x825433, 0xF2E5D0, true));
        return List.copyOf(forms);
    }

    private static void addBreed(List<Form> forms, String family, String breedId, String breed,
            EntityType<? extends Mob> entityType, int background, int pattern, String child, String female, String male) {
        String[] ids = {child + "_" + breedId, female + "_" + breedId, male + "_" + breedId};
        Kind[] kinds = {Kind.CHILD, Kind.FEMALE, Kind.MALE};
        for (int i = 0; i < kinds.length; i++)
            forms.add(new Form(ids[i], family, familyLabel(family), breed, kinds[i], entityType, background, pattern, false));
    }

    private static void addCowBreed(List<Form> forms, String breedId, String breed,
            EntityType<? extends Mob> entityType, int background, int pattern) {
        for (Kind kind : List.of(Kind.CHILD, Kind.FEMALE, Kind.MALE)) {
            String suffix = switch (kind) {
                case CHILD -> "calf";
                case FEMALE -> "cow";
                case MALE -> "bull";
                case RANDOM -> "random";
            };
            forms.add(new Form("cow_" + breedId + "_" + suffix, "cow", "Cow", breed,
                    kind, entityType, background, pattern, false));
        }
    }

    private static void addRandom(List<Form> forms, String family, String label,
            EntityType<? extends Mob> entityType, int background, int pattern) {
        forms.add(new Form(family + "_random", family, label, "", Kind.RANDOM,
                entityType, background, pattern, true));
    }

    private static String familyLabel(String family) {
        return switch (family) {
            case "cow" -> "Cow";
            case "draft_horse" -> "Draft Horse";
            default -> family.substring(0, 1).toUpperCase() + family.substring(1);
        };
    }

    private static String breedId(String breed) {
        return breed.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    private record Form(String id, String family, String familyLabel, String breed, Kind kind,
            EntityType<? extends Mob> entityType, int background, int pattern, boolean random) {}

    private enum Kind {
        CHILD("Child"), FEMALE("Female"), MALE("Male"), RANDOM("Random");
        private final String displayName;
        Kind(String displayName) { this.displayName = displayName; }
    }
}
