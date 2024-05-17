import { CommentResponse } from "./CommentResponse";

export interface ResponseContent {
    id?: number;
    title?: string;
    description?: string;
    estimatedHours?: number;
    imagePath?: string;
    comments?: CommentResponse[];
    orderInCourse?: number;
    completed?: boolean;
}