export interface RequestContent {
    title: string;
    description: string;
    estimatedHours: number;
    image?: File;
    teacherId: string;
}