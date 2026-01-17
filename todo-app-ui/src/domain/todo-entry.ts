import type {Category} from "@/domain/category.ts";
import type {PriorityEnum} from "@/domain/priority.enum.ts";

export interface TodoEntry {
    id?: number;
    title?: string;
    description?: string;
    done?: boolean;
    createdAt?: string;
    dueDate?: string;
    priority?: PriorityEnum;
    category?: Category | null;
}
