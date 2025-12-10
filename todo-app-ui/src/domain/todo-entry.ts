import type {PriorityEnum} from "@/domain/priority.enum.ts";

export interface TodoEntry {
    id?: number;
    description?: string;
    done?: boolean;
    dueDate?: Date;
    priority?: PriorityEnum;
}