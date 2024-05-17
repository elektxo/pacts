import { RequestContent } from "./RequestContent";

export interface CourseRequest {
    title: string;
    description: string;
    price: number;
    free: Boolean;
    teacherId?: string;
    contents?: RequestContent[];
    image?:File
}