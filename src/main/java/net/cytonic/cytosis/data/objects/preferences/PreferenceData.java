package net.cytonic.cytosis.data.objects.preferences;

import net.cytonic.cytosis.Cytosis;
import net.cytonic.cytosis.data.objects.TypedNamespace;
import net.cytonic.cytosis.managers.PreferenceManager;
import net.cytonic.cytosis.utils.Utils;

import java.util.Map;

/**
 * A class holding a user's preference data.
 */
public class PreferenceData {
    private final Map<TypedNamespace<?>, Preference<?>> preferences;

    /**
     * Creates a new PreferenceData object with the specified preferences
     *
     * @param preferences the preferences
     */
    public PreferenceData(Map<TypedNamespace<?>, Preference<?>> preferences) {
        this.preferences = preferences;
    }

    /**
     * Deserializes the string into a {@link PreferenceData} object. Effectivley the same as {@link #loadData(String)}, but this creates a new object
     *
     * @param data The serialized json data
     * @return a new {@link PreferenceData} object with the specified data
     */
    public static PreferenceData deserialize(String data) {
        Map<TypedNamespace<?>, Preference<?>> preferences = Cytosis.GSON.fromJson(data, Utils.PREFERENCE_MAP.getType()); // <NamespaceID, Preference>

        return new PreferenceData(preferences);
    }

    /**
     * Gets a preference of the specified type. If the player does not have a set preference, it will return the default value.
     *
     * @param namespaceID The namespace of the preference
     * @param <T>         The type of the preference
     * @return The preference. Null if it does not exist
     */
    public <T> T get(TypedNamespace<T> namespaceID) {
        if (preferences.containsKey(namespaceID)) {
            return (T) preferences.get(namespaceID).value();
        } else {
            return PreferenceManager.PREFERENCE_REGISTRY.get(namespaceID).preference().value();
        }
    }

    /**
     * Gets a preference of the specified type. If the player does not have a set preference, it will return the default value.
     *
     * @param namespace The namespaced ID to pull the namespace from
     * @param <T>       The type of the preference
     * @return The preference. Null if it does not exist
     */
    @SuppressWarnings("unchecked")
    public <T> T get(NamespacedPreference<T> namespace) {
        if (preferences.containsKey(namespace.typedNamespace())) {
            return (T) preferences.get(namespace.typedNamespace()).value();
        } else {
            return namespace.value();
        }
    }

    /**
     * Sets the value of the specified preference
     *
     * @param namespace The namespace of the preference
     * @param value     The new value
     * @param <T>       The type of the preference
     */
    public <T> void set(TypedNamespace<T> namespace, T value) {
        if (PreferenceManager.PREFERENCE_REGISTRY.isJson(namespace)) {
            preferences.put(namespace, new JsonPreference<>(namespace, value));
        } else preferences.put(namespace, new NamespacedPreference<>(namespace, value));
    }

    public <T> void set(NamespacedPreference<T> preference) {
        set(preference.typedNamespace(), preference.value());
    }

    /**
     * Loads the preference data from the specified json string
     *
     * @param data the json string produced from the {@link PreferenceData#serialize} method
     */
    public void loadData(String data) {
        preferences.putAll(deserialize(data).preferences);
    }

    /**
     * Serializes the preference data into a json string
     *
     * @return the json data of this object
     */
    public String serialize() {
        return Cytosis.GSON.toJson(preferences, Utils.PREFERENCE_MAP.getType());
    }


    @Override
    public String toString() {
        return "PreferenceData [preferences=" +
                preferences +
                "]";
    }
}
