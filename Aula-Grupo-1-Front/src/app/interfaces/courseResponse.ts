import { CommentResponse } from "./CommentResponse"
import { ResponseContent } from "./ResponseContent"

export interface CourseResponse {
    id?: number
    title: string
    description: string
    price : number
    free: boolean
    teacherId?:string
    teacherEmail?:string
    idReviewable?:number
    contents?: ResponseContent[]
    imagePath?:string
    comments?: CommentResponse[];
}