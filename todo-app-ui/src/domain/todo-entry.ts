import type {PriorityEnum} from "@/domain/priority.enum.ts";

export interface TodoEntry {
    id?: number;
    title?: string;
    description?: string;
    done?: boolean;
    dueDate?: Date | undefined;
    priority?: PriorityEnum;
}