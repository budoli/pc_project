import { create } from "zustand";
import {
  addFavoritePolicy,
  getFavoritePolicies,
  removeFavoritePolicy,
  type FavoritePolicy,
} from "../api/favoritePolicyApi";

type FavoritePolicyState = {
  userId: number | null;
  loading: boolean;
  pendingIds: number[];
  favorites: FavoritePolicy[];
  load: (userId: number) => Promise<void>;
  clear: () => void;
  toggle: (policy: FavoritePolicy) => Promise<void>;
};

export const useFavoritePolicyStore = create<FavoritePolicyState>((set, get) => ({
  userId: null,
  loading: false,
  pendingIds: [],
  favorites: [],
  load: async (userId) => {
    set({ userId, favorites: [], loading: true });
    try {
      const favorites = await getFavoritePolicies();
      if (get().userId === userId) set({ favorites });
    } finally {
      if (get().userId === userId) set({ loading: false });
    }
  },
  clear: () => set({ userId: null, favorites: [], loading: false, pendingIds: [] }),
  toggle: async (policy) => {
    const userId = get().userId;
    if (userId === null) throw new Error("로그인이 필요합니다.");
    if (get().pendingIds.includes(policy.id)) return;
    set((state) => ({ pendingIds: [...state.pendingIds, policy.id] }));
    const isFavorite = get().favorites.some((item) => item.id === policy.id);
    try {
      if (isFavorite) {
        await removeFavoritePolicy(policy.id);
        if (get().userId === userId) {
          set((state) => ({ favorites: state.favorites.filter((item) => item.id !== policy.id) }));
        }
      } else {
        await addFavoritePolicy(policy.id);
        if (get().userId === userId) {
          set((state) => ({ favorites: [policy, ...state.favorites] }));
        }
      }
    } finally {
      set((state) => ({ pendingIds: state.pendingIds.filter((id) => id !== policy.id) }));
    }
  },
}));
